
package acme.features.flightCrewMember.visaRequirements;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.visaRequirements.VisaRequirements;
import acme.realms.FlightCrewMember;

@Controller
public class FlightCrewMemberVisaRequirementsController extends AbstractGuiController<FlightCrewMember, VisaRequirements> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberVisaRequirementsListService	listService;

	@Autowired
	private FlightCrewMemberVisaRequirementsShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
