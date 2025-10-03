
package acme.features.flightCrewMember.visaRequirements;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.visaRequirements.VisaRequirements;

@Repository
public interface FlightCrewMemberVisaRequirementsRepository extends AbstractRepository {

	@Query("select v from VisaRequirements v where v.assignment.member.id = :id")
	Collection<VisaRequirements> findVisaRequirementsByMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.member.id = :id")
	Collection<FlightAssignment> findFlightAssignmentByMemberId(int id);

	@Query("select distinct fa.leg.departureAirport.country, fa.leg.arrivalAirport.country from FlightAssignment fa where fa.member.id = :id")
	Collection<String[]> findAirportsLegByMemberId(int id);

	@Query("select v from VisaRequirements v where v.id = :id")
	VisaRequirements findById(int id);
}
