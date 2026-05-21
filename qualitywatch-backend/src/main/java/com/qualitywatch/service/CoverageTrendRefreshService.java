package com.qualitywatch.service;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoverageTrendRefreshService {

    private static final Logger log = LoggerFactory.getLogger(CoverageTrendRefreshService.class);

    private final EntityManager entityManager;

    public CoverageTrendRefreshService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void refreshCoverageTrends() {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW coverage_trends").executeUpdate();
        log.debug("Refreshed coverage_trends materialized view");
    }
}
