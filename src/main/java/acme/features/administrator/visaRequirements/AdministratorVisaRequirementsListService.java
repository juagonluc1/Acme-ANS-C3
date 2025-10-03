
package acme.features.administrator.visaRequirements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
				super.state(false, "*", "administrator.visa-requirements.error.remote");
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
			super.state(false, "*", "administrator.visa-requirements.error.unexpected");
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return vr;

	}

	@Override
	public void load() {
		for (FlightAssignment f : this.repository.findFlightAssignment())
			this.repository.deleteByAssignment(f);
		for (FlightAssignment f : this.repository.findFlightAssignment()) {
			String origin = f.getLeg().getDepartureAirport().getCountry();
			String origin2 = this.conversionNameCode(origin.toUpperCase());
			String destination = f.getLeg().getArrivalAirport().getCountry();
			String destination2 = this.conversionNameCode(destination.toUpperCase());
			VisaRequirements visa = this.haveInformation(origin2, destination2);
			if (visa == null)
				continue;
			else {
				visa.setAssignment(f);
				super.getBuffer().addData(visa);
				this.repository.save(visa);
			}
		}
	}

	@Override
	public void unbind(final VisaRequirements object) {
		assert object != null;

		Dataset dataset = super.unbindObject(object, "passportName", "destinationName", "assignment", "nameCategory", "duration");
		dataset.put("passportName", object.getAssignment().getLeg().getDepartureAirport().getCountry());
		dataset.put("destinationName", object.getAssignment().getLeg().getArrivalAirport().getCountry());
		dataset.put("assignment", object.getAssignment().getId());
		dataset.put("nameCategory", object.getNameCategory());
		dataset.put("duration", object.getDuration());

		super.getResponse().addData(dataset);
	}

	private String conversionNameCode(final String name) {
		Map<String, String> nombreCode = new HashMap<>();

		nombreCode.put("UNITED KINGDOM", "GB");

		nombreCode.put("ALGERIA", "DZ");
		nombreCode.put("BELIZE", "BZ");
		nombreCode.put("CENTRAL AFRICAN REPUBLIC", "CF");
		nombreCode.put("CHAD", "TD");
		nombreCode.put("CHINA", "CN");
		nombreCode.put("CONGO", "CG");
		nombreCode.put("DOMINICAN REPUBLIC", "DO");
		nombreCode.put("ERITREA", "ER");
		nombreCode.put("GHANA", "GH");
		nombreCode.put("HONDURAS", "HN");
		nombreCode.put("IRAN", "IR");
		nombreCode.put("IRELAND", "IE");
		nombreCode.put("JAMAICA", "JM");
		nombreCode.put("LESOTHO", "LS");
		nombreCode.put("LIBERIA", "LR");
		nombreCode.put("MALAYSIA", "MY");
		nombreCode.put("MALI", "ML");
		nombreCode.put("NAURU", "NR");
		nombreCode.put("NIGER", "NE");
		nombreCode.put("NORTH KOREA", "KP");
		nombreCode.put("PALESTINE", "PS");
		nombreCode.put("RUSSIA", "RU");
		nombreCode.put("SINGAPORE", "SG");
		nombreCode.put("SUDAN", "SD");
		nombreCode.put("SURINAME", "SR");
		nombreCode.put("TRINIDAD AND TOBAGO", "TT");
		nombreCode.put("TURKMENISTAN", "TM");
		nombreCode.put("YEMEN", "YE");
		nombreCode.put("AFGHANISTAN", "AF");

		nombreCode.put("BAHRAIN", "BH");
		nombreCode.put("BANGLADESH", "BD");
		nombreCode.put("BURKINA FASO", "BF");
		nombreCode.put("BURUNDI", "BI");
		nombreCode.put("CAMBODIA", "KH");
		nombreCode.put("COMOROS", "KM");
		nombreCode.put("DJIBOUTI", "DJ");
		nombreCode.put("EGYPT", "EG");
		nombreCode.put("ETHIOPIA", "ET");
		nombreCode.put("GUINEA-BISSAU", "GW");
		nombreCode.put("INDONESIA", "ID");
		nombreCode.put("IRAQ", "IQ");
		nombreCode.put("JORDAN", "JO");
		nombreCode.put("KUWAIT", "KW");
		nombreCode.put("LAOS", "LA");
		nombreCode.put("LEBANON", "LB");
		nombreCode.put("MADAGASCAR", "MG");
		nombreCode.put("MALDIVES", "MV");
		nombreCode.put("MAURITANIA", "MR");
		nombreCode.put("NEPAL", "NP");
		nombreCode.put("OMAN", "OM");
		nombreCode.put("PALAU", "PW");
		nombreCode.put("QATAR", "QA");
		nombreCode.put("SAMOA", "WS");
		nombreCode.put("SAUDI ARABIA", "SA");
		nombreCode.put("SIERRA LEONE", "SL");
		nombreCode.put("SOLOMON ISLANDS", "SB");
		nombreCode.put("SOMALIA", "SO");
		nombreCode.put("SRI LANKA", "LK");
		nombreCode.put("TAJIKISTAN", "TJ");
		nombreCode.put("TANZANIA", "TZ");
		nombreCode.put("TIMOR-LESTE", "TL");
		nombreCode.put("TONGA", "TO");
		nombreCode.put("TUVALU", "TV");
		nombreCode.put("ZIMBABWE", "ZW");

		nombreCode.put("ALBANIA", "AL");
		nombreCode.put("ANDORRA", "AD");
		nombreCode.put("ANGOLA", "AO");
		nombreCode.put("ANTIGUA AND BARBUDA", "AG");
		nombreCode.put("ARGENTINA", "AR");
		nombreCode.put("ARMENIA", "AM");
		nombreCode.put("AUSTRIA", "AT");
		nombreCode.put("BAHAMAS", "BS");
		nombreCode.put("BARBADOS", "BB");
		nombreCode.put("BELARUS", "BY");
		nombreCode.put("BELGIUM", "BE");
		nombreCode.put("BOLIVIA", "BO");
		nombreCode.put("BOSNIA AND HERZEGOVINA", "BA");
		nombreCode.put("BOTSWANA", "BW");
		nombreCode.put("BRAZIL", "BR");
		nombreCode.put("BRUNEI", "BN");
		nombreCode.put("BULGARIA", "BG");
		nombreCode.put("CAPE VERDE", "CV");
		nombreCode.put("CHILE", "CL");
		nombreCode.put("COLOMBIA", "CO");
		nombreCode.put("COSTA RICA", "CR");
		nombreCode.put("CROATIA", "HR");
		nombreCode.put("CYPRUS", "CY");
		nombreCode.put("CZECH REPUBLIC", "CZ");
		nombreCode.put("DENMARK", "DK");
		nombreCode.put("DOMINICA", "DM");
		nombreCode.put("ECUADOR", "EC");
		nombreCode.put("EL SALVADOR", "SV");
		nombreCode.put("ESTONIA", "EE");
		nombreCode.put("SWAZILAND", "SZ");
		nombreCode.put("FIJI", "FJ");
		nombreCode.put("FINLAND", "FI");
		nombreCode.put("FRANCE", "FR");
		nombreCode.put("GAMBIA", "GM");
		nombreCode.put("GEORGIA", "GE");
		nombreCode.put("GERMANY", "DE");
		nombreCode.put("GREECE", "GR");
		nombreCode.put("GRENADA", "GD");
		nombreCode.put("GUATEMALA", "GT");
		nombreCode.put("GUYANA", "GY");
		nombreCode.put("HAITI", "HT");
		nombreCode.put("HONG KONG", "HK");
		nombreCode.put("HUNGARY", "HU");
		nombreCode.put("ICELAND", "IS");
		nombreCode.put("ISRAEL", "IL");
		nombreCode.put("ITALY", "IT");
		nombreCode.put("JAPAN", "JP");
		nombreCode.put("KAZAKHSTAN", "KZ");
		nombreCode.put("KIRIBATI", "KI");
		nombreCode.put("KOSOVO", "XK");
		nombreCode.put("KYRGYZSTAN", "KG");
		nombreCode.put("LATVIA", "LV");
		nombreCode.put("LIECHTENSTEIN", "LI");
		nombreCode.put("LITHUANIA", "LT");
		nombreCode.put("LUXEMBOURG", "LU");
		nombreCode.put("MACAO", "MO");
		nombreCode.put("MALAWI", "MW");
		nombreCode.put("MALTA", "MT");
		nombreCode.put("MARSHALL ISLANDS", "MH");
		nombreCode.put("MAURITIUS", "MU");
		nombreCode.put("MEXICO", "MX");
		nombreCode.put("MICRONESIA", "FM");
		nombreCode.put("MOLDOVA", "MD");
		nombreCode.put("MONACO", "MC");
		nombreCode.put("MONGOLIA", "MN");
		nombreCode.put("MONTENEGRO", "ME");
		nombreCode.put("MOROCCO", "MA");
		nombreCode.put("MOZAMBIQUE", "MZ");
		nombreCode.put("NAMIBIA", "NA");
		nombreCode.put("NETHERLANDS", "NL");
		nombreCode.put("NICARAGUA", "NI");
		nombreCode.put("NORTH MACEDONIA", "MK");
		nombreCode.put("NORWAY", "NO");
		nombreCode.put("PANAMA", "PA");
		nombreCode.put("PARAGUAY", "PY");
		nombreCode.put("PERU", "PE");
		nombreCode.put("PHILIPPINES", "PH");
		nombreCode.put("POLAND", "PL");
		nombreCode.put("PORTUGAL", "PT");
		nombreCode.put("ROMANIA", "RO");
		nombreCode.put("RWANDA", "RW");
		nombreCode.put("SAINT KITTS AND NEVIS", "KN");
		nombreCode.put("SAINT LUCIA", "LC");
		nombreCode.put("SAN MARINO", "SM");
		nombreCode.put("SAO TOME AND PRINCIPE", "ST");
		nombreCode.put("SENEGAL", "SN");
		nombreCode.put("SERBIA", "RS");
		nombreCode.put("SEYCHELLES", "SC");
		nombreCode.put("SLOVAKIA", "SK");
		nombreCode.put("SLOVENIA", "SI");
		nombreCode.put("SOUTH AFRICA", "ZA");
		nombreCode.put("SOUTH KOREA", "KR");
		nombreCode.put("SPAIN", "ES");
		nombreCode.put("SAINT VINCENT AND THE GRENADINES", "VC");
		nombreCode.put("SWEDEN", "SE");
		nombreCode.put("SWITZERLAND", "CH");
		nombreCode.put("TAIWAN", "TW");
		nombreCode.put("THAILAND", "TH");
		nombreCode.put("TUNISIA", "TN");
		nombreCode.put("TURKEY", "TR");
		nombreCode.put("UKRAINE", "UA");
		nombreCode.put("UNITED ARAB EMIRATES", "AE");
		nombreCode.put("URUGUAY", "UY");
		nombreCode.put("UZBEKISTAN", "UZ");
		nombreCode.put("VANUATU", "VU");
		nombreCode.put("VATICAN", "VA");
		nombreCode.put("VENEZUELA", "VE");
		nombreCode.put("VIETNAM", "VN");
		nombreCode.put("ZAMBIA", "ZM");

		nombreCode.put("AUSTRALIA", "AU");
		nombreCode.put("AZERBAIJAN", "AZ");
		nombreCode.put("BENIN", "BJ");
		nombreCode.put("BHUTAN", "BT");
		nombreCode.put("CAMEROON", "CM");
		nombreCode.put("CANADA", "CA");
		nombreCode.put("DR CONGO", "CD");
		nombreCode.put("IVORY COAST", "CI");
		nombreCode.put("CUBA", "CU");
		nombreCode.put("EQUATORIAL GUINEA", "GQ");
		nombreCode.put("GABON", "GA");
		nombreCode.put("GUINEA", "GN");
		nombreCode.put("INDIA", "IN");
		nombreCode.put("KENYA", "KE");
		nombreCode.put("LIBYA", "LY");
		nombreCode.put("MYANMAR", "MM");
		nombreCode.put("NEW ZEALAND", "NZ");
		nombreCode.put("NIGERIA", "NG");
		nombreCode.put("PAKISTAN", "PK");
		nombreCode.put("PAPUA NEW GUINEA", "PG");
		nombreCode.put("SOUTH SUDAN", "SS");
		nombreCode.put("SYRIA", "SY");
		nombreCode.put("TOGO", "TG");
		nombreCode.put("UGANDA", "UG");
		nombreCode.put("UNITED STATES", "US");

		return nombreCode.get(name);

	}
}
