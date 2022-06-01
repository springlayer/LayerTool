package org.springlayer.core.flowable.service;

import org.apache.commons.lang3.StringUtils;
import org.flowable.app.api.AppRepositoryService;
import org.flowable.app.api.repository.AppDeploymentBuilder;
import org.flowable.ui.common.properties.FlowableCommonAppProperties;
import org.flowable.ui.common.tenant.TenantProvider;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.flowable.ui.modeler.service.AppDefinitionPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description
 **/
@Primary
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AppDefinitionPublishServiceImpl extends AppDefinitionPublishService {

    private static final Logger logger = LoggerFactory.getLogger(AppDefinitionPublishServiceImpl.class);

    @Resource
    protected AppRepositoryService appRepositoryService;

    @Resource
    protected TenantProvider tenantProvider;

    public AppDefinitionPublishServiceImpl(FlowableCommonAppProperties properties, FlowableModelerAppProperties modelerAppProperties) {
        super(properties, modelerAppProperties);
    }


    /**
     * 參照 AppDeploymentCollectionResource.uploadDeployment
     *
     * @param artifactName   ignore
     * @param zipArtifact    ignore
     * @param deploymentKey  ignore
     * @param deploymentName ignore
     */
    @Override
    protected void deployZipArtifact(String artifactName, byte[] zipArtifact, String deploymentKey, String deploymentName) {
        logger.debug("发布流程 deploymentKey={}, deploymentName={}", deploymentKey, deploymentName);
        AppDeploymentBuilder deploymentBuilder = appRepositoryService.createDeployment();
        deploymentBuilder.addZipInputStream(new ZipInputStream(new ByteArrayInputStream(zipArtifact)));
        deploymentBuilder.key(deploymentKey);
        String tenantId = tenantProvider.getTenantId();
        if (StringUtils.isNotEmpty(tenantId)) {
            deploymentBuilder.tenantId(tenantId);
        }

        deploymentBuilder.category(deploymentKey);
        deploymentBuilder.name(artifactName);
        deploymentBuilder.deploy();
    }

}
