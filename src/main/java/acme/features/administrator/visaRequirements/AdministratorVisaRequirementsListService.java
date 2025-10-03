
package acme.features.administrator.visaRequirements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.visaRequirements.VisaRequirements;

@GuiService
public class AdministratorVisaRequirementsListService extends AbstractGuiService<Administrator, VisaRequirements> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorVisaRequirementsRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	public VisaRequirements haveInformation(final String origin, final String destination) {
		HttpURLConnection conn = null;
		VisaRequirements vr = new VisaRequirements();
		try {
			String urlStr = "https://rough-sun-2523.fly.dev/visa/" + origin + "/" + destination;
			conn = (HttpURLConnection) new URL(urlStr).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			int code = conn.getResponseCode();
			if (code != 200) {
				super.state(false, "*", "flight-crew-member.visa-requirements.error.remote");
				return null;
			}

			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				for (String line; (line = br.readLine()) != null;)
					sb.append(line);
			}

			ObjectMapper mapper = new ObjectMapper();
			com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(sb.toString());

			// Mapeo manual JSON -> entidad
			vr.setNameCategory(root.path("category").path("name").asText(null));     // category.name -> nameCategory
			vr.setPassportName(root.path("passport").path("name").asText(null));
			vr.setDestinationName(root.path("destination").path("name").asText(null));
			if (root.has("dur") && root.get("dur").canConvertToInt())
				vr.setDuration(root.get("dur").asInt());                             // dur -> duration

			String lu = root.path("last_updated").asText(null);                      // last_updated -> lastUpdate
			if (lu != null && !lu.isBlank())
				vr.setLastUpdate(java.util.Date.from(java.time.Instant.parse(lu)));
		} catch (Exception e) {
			super.state(false, "*", "flight-crew-member.visa-requirements.error.unexpected");
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return vr;

	}

	@Override
	public void load() {
		for (FlightAssignment f : this.repository.findFlightAssignment()) {
			this.repository.deleteByAssignment(f); // borra SOLO la fila hija
			String origin = f.getLeg().getDepartureAirport().getCountry();
			String destination = f.getLeg().getArrivalAirport().getCountry();
			VisaRequirements visa = this.haveInformation("VA", "GB");
			visa.setAssignment(f);
			super.getBuffer().addData(visa);
			this.repository.save(visa);
		}
	}

	@Override
	public void unbind(final VisaRequirements object) {
		assert object != null;

		Dataset dataset = super.unbindObject(object, "passportName", "destinationName", "assignment");
		dataset.put("passportName", object.getAssignment().getLeg().getDepartureAirport().getCountry());
		dataset.put("destinationName", object.getAssignment().getLeg().getArrivalAirport().getCountry());
		dataset.put("assignment", object.getAssignment().getId());

		super.getResponse().addData(dataset);
	}

}
