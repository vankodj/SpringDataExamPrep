package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Picture;

import java.util.List;
import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture,Long> {

    Optional<Picture> findByName(String name);

    Optional<List<Picture>> findAllByCar(Car car);



}
