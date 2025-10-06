
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean isAuthorised = false;

		if (super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class)) {

			if (super.getRequest().getMethod().equals("GET") && super.getRequest().getData("assignmentId", Integer.class) != null) {

				Integer assignmentId = super.getRequest().getData("assignmentId", Integer.class);
				FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(assignmentId);

				if (flightAssignment != null) {

					FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

					isAuthorised = !flightAssignment.isDraftMode() && flightAssignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()) && flightAssignment.getMember().equals(flightCrewMember);
				}

			}

			// Only is allowed to create an activity log if the creator is the flight crew member associated to the flight assignment.
			// An activity log cannot be created if the assignment is planned, only complete are allowed.
			if (super.getRequest().getMethod().equals("POST") && super.getRequest().getData("assignmentId", Integer.class) != null && super.getRequest().getData("id", Integer.class) != null) {

				Integer assignmentId = super.getRequest().getData("assignmentId", Integer.class);
				FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(assignmentId);

				if (flightAssignment != null && super.getRequest().getData("id", Integer.class).equals(0)) {

					FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

					isAuthorised = !flightAssignment.isDraftMode() && flightAssignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()) && flightAssignment.getMember().equals(flightCrewMember);
				}

			}
		}

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", int.class);
		super.getResponse().addGlobal("masterId", masterId);

		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setAssignment(flightAssignment);
		activityLog.setMoment(MomentHelper.getCurrentMoment());
		activityLog.setDraftMode(true);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog object) {
		super.bindObject(object, "logType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog object) {

		boolean momentIsNull = object.getMoment() != null;
		super.state(momentIsNull, "moment", "acme.validation.activityLog.flightCrewMember.momentNull");

		/*
		 * if (object.getAssignment() != null) {
		 * boolean correctAssign = object.getAssignment().getStatus().equals(Status.LANDED);
		 * super.state(!correctAssign, "assignment", "acme.validation.activityLog.assign");
		 * }
		 */
		assert object != null;

	}

	@Override
	public void perform(final ActivityLog object) {
		assert object != null;
		object.setMoment(MomentHelper.getCurrentMoment());
		this.repository.save(object);

	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();

	}

	@Override
	public void unbind(final ActivityLog object) {

		Dataset dataset;

		dataset = super.unbindObject(object, "moment", "logType", "description", "severityLevel", "draftMode", "assignment");

		if (object.getAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		dataset.put("assignmet", super.getRequest().getData("assignment", int.class));
		super.getResponse().addData(dataset);

	}

}
