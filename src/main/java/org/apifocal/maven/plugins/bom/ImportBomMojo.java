/*
 * Copyright 2017 apifocal LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apifocal.maven.plugins.bom;

import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;

/**
 * Imports a BOM project's dependency management, but also the declared properties.
 */
@Mojo(name = "import-bom", defaultPhase = LifecyclePhase.INITIALIZE)
public class ImportBomMojo extends AbstractBomMojo {

    @Parameter(required = true)
    protected Dependency bomArtifact;

    @Override
    public void execute() throws MojoExecutionException {
        addBomImport(bomArtifact.clone());
        addBomDependency(bomArtifact.clone());
        importProperties(bomArtifact);
    }

    protected void addBomDependency(Dependency artifact) {
        artifact.setScope(RESOLUTIONSCOPE_PROVIDED);
        project.getModel().addDependency(artifact.clone());
    }

    /**
     * Use the usual maven bom processing to import dependencyManagement from the bom project.

     * @param artifact
     */
    protected void addBomImport(Dependency artifact) {
        DependencyManagement dependencyManagement = project.getDependencyManagement();
        if (dependencyManagement == null) {
            dependencyManagement = new DependencyManagement();
            project.getModel().setDependencyManagement(dependencyManagement);
        }
        artifact.setScope(RESOLUTIONSCOPE_IMPORT);
        dependencyManagement.addDependency(artifact.clone());
    }

}
