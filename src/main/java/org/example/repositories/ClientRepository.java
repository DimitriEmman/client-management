package org.example.repositories;

import org.example.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c.profession, AVG(CAST(c.salary AS DOUBLE)) FROM Client c GROUP BY c.profession")
    List<Object[]> calculateAverageSalaryByProfession();
}
