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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Import properties of all declared BOM imports.
 *
 * Scan the current project of dependencyManagement dependencies with import scope, and recursively
 * imports properties from these projects.
 *
 * @todo For now the maven model does not expose dependencyManagement entries with scope = import. This
 * goal can only be used after this is fixed upstream.
 */
@Mojo(name = "import-boms", defaultPhase = LifecyclePhase.INITIALIZE)
public class ImportBoms extends AbstractBomMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        throw new MojoExecutionException("The goal import-boms is not yet working");
    }

    // this
    public void doExecute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("project model: " + project.getModel());

        DependencyManagement dependencyManagement = project.getDependencyManagement();
        if (dependencyManagement == null) {
            getLog().warn("No dependencyManagement section found");
            return;
        }

        dependencyManagement.getDependencies().stream()
                .forEach((dep) -> getLog().debug("found dependencyManagement: " + dep));

        try {
            long count = dependencyManagement.getDependencies().stream()
                    .filter((Dependency dep) -> RESOLUTIONSCOPE_IMPORT.equals(dep.getScope()))
                    .map((dep) -> {
                        getLog().debug("found imported dependency: " + dep);
                        return dep;
                    })
                    .map((Dependency dep) -> {
                        try {
                            importProperties(dep);
                            return dep;
                        } catch (MojoExecutionException ex) {
                            throw new LambdaMojoExecutionException(ex);
                        }
                    })
                    .count();
            getLog().info("Imported properties from " + count + " dependencies");
        } catch (LambdaMojoExecutionException ex) {
            ex.throwCause();
        }
    }

}
