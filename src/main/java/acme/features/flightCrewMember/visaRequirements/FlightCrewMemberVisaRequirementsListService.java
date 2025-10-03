
package acme.features.flightCrewMember.visaRequirements;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.visaRequirements.VisaRequirements;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberVisaRequirementsListService extends AbstractGuiService<FlightCrewMember, VisaRequirements> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberVisaRequirementsRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id;
		id = super.getRequest().getPrincipal().getActiveRealm().getId();
		super.getBuffer().addData(this.repository.findVisaRequirementsByMemberId(id));
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

}
