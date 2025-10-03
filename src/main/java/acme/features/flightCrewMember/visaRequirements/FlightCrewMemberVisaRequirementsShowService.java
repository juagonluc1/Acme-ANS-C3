
package acme.features.flightCrewMember.visaRequirements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.visaRequirements.VisaRequirements;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberVisaRequirementsShowService extends AbstractGuiService<FlightCrewMember, VisaRequirements> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberVisaRequirementsRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		HttpURLConnection conn = null;
		try {
			String urlStr = "https://rough-sun-2523.fly.dev/visa/VA/GB";
			conn = (HttpURLConnection) new URL(urlStr).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			int code = conn.getResponseCode();
			if (code != 200) {
				super.state(false, "*", "flight-crew-member.visa-requirements.error.remote");
				return;
			}

			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				for (String line; (line = br.readLine()) != null;)
					sb.append(line);
			}

			ObjectMapper mapper = new ObjectMapper();
			com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(sb.toString());

			// Mapeo manual JSON -> entidad
			VisaRequirements vr = new VisaRequirements();
			vr.setNameCategory(root.path("category").path("name").asText(null));     // category.name -> nameCategory
			vr.setPassportName(root.path("passport").path("name").asText(null));
			vr.setDestinationName(root.path("destination").path("name").asText(null));
			if (root.has("dur") && root.get("dur").canConvertToInt())
				vr.setDuration(root.get("dur").asInt());                             // dur -> duration

			String lu = root.path("last_updated").asText(null);                      // last_updated -> lastUpdate
			if (lu != null && !lu.isBlank())
				vr.setLastUpdate(java.util.Date.from(java.time.Instant.parse(lu)));

			super.getBuffer().addData(vr);

		} catch (Exception e) {
			super.state(false, "*", "flight-crew-member.visa-requirements.error.unexpected");
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}

	@Override
	public void unbind(final VisaRequirements object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "nameCategory", "duration", "lastUpdate", "passportName", "destinationName");
		super.getResponse().addData(dataset);
	}

}
