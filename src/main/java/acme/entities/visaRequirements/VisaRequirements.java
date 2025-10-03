
package acme.entities.visaRequirements;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.flightAssignment.FlightAssignment;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class VisaRequirements extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 250)
	@Automapped
	private String				passportName;

	@Mandatory
	@ValidString(max = 250)
	@Automapped
	private String				destinationName;

	@Mandatory
	@ValidString(max = 250)
	@Automapped
	private String				nameCategory;

	@Optional
	@ValidNumber(min = 0, max = 100000)
	@Automapped
	private Integer				duration;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdate;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private FlightAssignment	assignment;
}
