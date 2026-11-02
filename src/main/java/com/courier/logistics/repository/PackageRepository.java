package com.courier.logistics.repository;

import com.courier.logistics.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Long> {
    List<PackageEntity> findByPackageTypeIgnoreCase(String packageType);
}
