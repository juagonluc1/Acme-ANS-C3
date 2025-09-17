
package acme.features.administrator.airport;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport.Airport;

@Repository
public interface AdministratorAirportRepository extends AbstractRepository {

	@Query("SELECT a FROM Airport a")
	Collection<Airport> findAllAirports();

	@Query("SELECT a FROM Airport a WHERE a.id = :id")
	Airport findAirportById(int id);

	@Query("SELECT a FROM Airport a WHERE a.iataCode = :iataCode")
	Collection<Airport> findAirportsByIataCode(@Param("iataCode") String iataCode);
}
