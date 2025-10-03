
package acme.features.administrator.visaRequirements;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.visaRequirements.VisaRequirements;

@Repository
public interface AdministratorVisaRequirementsRepository extends AbstractRepository {

	@Query("select v from VisaRequirements v")
	Collection<VisaRequirements> findVisaRequirements();

	@Query("select fa from FlightAssignment fa")
	Collection<FlightAssignment> findFlightAssignment();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from VisaRequirements v where v.assignment = :assignment")
	void deleteByAssignment(FlightAssignment assignment);

	@Query("select v from VisaRequirements v where v.id = :id")
	VisaRequirements findById(int id);
}
