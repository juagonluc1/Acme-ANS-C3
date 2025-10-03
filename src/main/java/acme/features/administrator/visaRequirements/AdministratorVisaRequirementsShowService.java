
package acme.features.administrator.visaRequirements;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.visaRequirements.VisaRequirements;

@GuiService
public class AdministratorVisaRequirementsShowService extends AbstractGuiService<Administrator, VisaRequirements> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorVisaRequirementsRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class); // viene del list
		VisaRequirements vr = this.repository.findById(assignmentId);

		super.state(vr != null, "*", "administrator.visa-requirements.error.not-found");
		if (vr != null)
			super.getBuffer().addData(vr);
	}

	@Override
	public void unbind(final VisaRequirements object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "nameCategory", "duration", "lastUpdate", "passportName", "destinationName");
		super.getResponse().addData(dataset);
	}

}
