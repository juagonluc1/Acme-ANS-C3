
package acme.features.administrator.visaRequirements;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.entities.visaRequirements.VisaRequirements;

@Controller
public class AdministratorVisaRequirementsController extends AbstractGuiController<Administrator, VisaRequirements> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorVisaRequirementsListService	listService;

	@Autowired
	private AdministratorVisaRequirementsUpdateService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("update", this.updateService);
	}

}
