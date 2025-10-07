
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		int flightAssignmentId = super.getRequest().getData("masterId", int.class);
		System.out.println("9");

		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		status = flightAssignment != null && flightAssignment.getMember().getId() == super.getRequest().getPrincipal().getActiveRealm().getId() && !flightAssignment.isDraftMode();
		System.out.println("10");

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<ActivityLog> objects;
		System.out.println("1");
		int flightAssignmentId = super.getRequest().getData("masterId", int.class);
		System.out.println("2");

		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		super.getResponse().addGlobal("masterId", flightAssignmentId);
		System.out.println("3");

		objects = this.repository.findActivityLogsByFlightAssignmentId(flightAssignmentId);
		System.out.println("4");

		super.getResponse().addGlobal("masterDraftMode", flightAssignment.isDraftMode());
		System.out.println("5");

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final ActivityLog object) {
		assert object != null;

		Dataset dataset;
		System.out.println("6");

		dataset = super.unbindObject(object, "moment", "logType", "description", "severityLevel", "draftMode");
		System.out.println("7");

		if (object.isDraftMode())
			dataset.put("draftMode", "✓");
		else
			dataset.put("draftMode", "✗");
		System.out.println("8");

		super.getResponse().addData(dataset);
		System.out.println("11");

	}

}
