package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.dto.view.NextMaintenanceView;
import com.example.serviceBookBackend.entity.MaintenanceJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceJobsRepository extends JpaRepository<MaintenanceJobEntity, Integer> {
    List<MaintenanceJobEntity> findAllByCarId(Integer carId);

    @Query(value = """
                SELECT
                    mj.id AS jobId,
                    mj.name AS jobName,
                    mj.frequency AS frequency,
                    mj.is_regular AS isRegular,
                    c.odometer AS currentCarOdometer,
                    COALESCE(MAX(m.odometer), 0) AS lastPerformedOdometer,
                    (COALESCE(MAX(m.odometer), 0) + mj.frequency) - c.odometer AS kmRemaining
                FROM maintenance_jobs mj
                JOIN cars c ON mj.car = c.id
                LEFT JOIN performed_maintenance pm ON mj.id = pm.maintenance_job_id
                LEFT JOIN maintenance m ON pm.maintenance_id = m.id
                WHERE c.id = :carId
                GROUP BY mj.id, mj.name, mj.frequency, c.odometer
                ORDER BY kmRemaining ASC
            """, nativeQuery = true)
    List<NextMaintenanceView> findAllNextMaintenancesByCarId(@Param("carId") Integer carId);

}
