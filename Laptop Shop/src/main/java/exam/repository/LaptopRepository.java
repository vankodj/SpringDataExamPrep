package exam.repository;

import exam.model.entities.Laptop;
import exam.model.entities.WarrantyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop,Long> {

    Optional<Laptop> findByMacAddress(String macAddress);

    @Query("select l from Laptop l order by l.cpuSpeed desc,l.ram desc ,l.storage desc, l.macAddress")
    List<Laptop> findAlLaptops();
}
