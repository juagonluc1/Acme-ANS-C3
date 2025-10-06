
package acme.features.flightCrewMember.activityLog;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);
		status = activityLog != null && activityLog.isDraftMode() && activityLog.getAssignment().getMember().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog log;
		int id;

		id = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(id);

		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog object) {
		assert object != null;
		super.bindObject(object, "moment", "logType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog object) {

		boolean momentIsNull = object.getMoment() != null;
		super.state(momentIsNull, "moment", "acme.validation.activityLog.flightCrewMember.momentNull");

		if (object.getMoment() != null) {
			Date dateFlightAssignment = this.repository.findFlightAssignmentById(object.getAssignment().getId()).getLastUpdate();
			Date dateLeg = this.repository.findFlightAssignmentById(object.getAssignment().getId()).getLeg().getScheduledArrival();
			boolean correctMoments = object.getMoment().after(dateFlightAssignment) && object.getMoment().after(dateLeg);
			super.state(correctMoments, "*", "acme.validation.activityLog.moment");
		}

		assert object != null;
	}

	@Override
	public void perform(final ActivityLog object) {
		assert object != null;
		object.setDraftMode(false);
		object.setMoment(MomentHelper.getCurrentMoment());
		this.repository.save(object);
	}

	@Override
	public void unbind(final ActivityLog object) {

		Dataset dataset;

		dataset = super.unbindObject(object, "moment", "logType", "description", "severityLevel", "draftMode", "assignment");
		super.getResponse().addData(dataset);

	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
