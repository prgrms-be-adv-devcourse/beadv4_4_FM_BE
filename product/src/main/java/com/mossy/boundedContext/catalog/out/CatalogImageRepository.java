package com.mossy.boundedContext.catalog.out;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.querydsl.core.group.GroupBy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogImageRepository extends JpaRepository<CatalogImage, Long> {
    List<CatalogImage> findByIsThumbnailTrue();

    Optional<CatalogImage> findByTargetIdAndIsThumbnailTrue(Long targetId);
}
