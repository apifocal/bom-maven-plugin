package org.apifocal.maven.plugins.bom;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Imports a BOM project's dependency management, but also the declared properties.
 */
@Mojo(name = "import-bom", defaultPhase = LifecyclePhase.INITIALIZE)
public class ImportBomMojo extends AbstractMojo {

    protected static final String RESOLUTIONSCOPE_IMPORT = "import";

    @Parameter(required = true)
    protected Dependency bomArtifact;

    @Parameter(defaultValue = "false", required = true)
    protected boolean recursive;

    //// injected from maven
    ////
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${localRepository}")
    protected ArtifactRepository localRepo;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}")
    protected List<ArtifactRepository> remoteRepos;

    @Component
    protected ArtifactResolver artifactResolver;

    @Component
    protected ArtifactFactory artifactFactory;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // use the usual maven bom processing to import dependencyManagement from the bom project
            DependencyManagement dependencyManagement = project.getDependencyManagement();
            if (dependencyManagement == null) {
                dependencyManagement = new DependencyManagement();
                project.getModel().setDependencyManagement(dependencyManagement);
            }
            bomArtifact.setScope(RESOLUTIONSCOPE_IMPORT);
            dependencyManagement.addDependency(bomArtifact);

            // add properties
            Artifact bom = artifactFactory.createProjectArtifact(
                    bomArtifact.getGroupId(),
                    bomArtifact.getArtifactId(),
                    bomArtifact.getVersion());

            processProperties(bom);
        } catch (IOException | ArtifactResolutionException | ArtifactNotFoundException ex) {
//            getLog().error("Failed to resolve BOM artifact", ex);
            throw new MojoExecutionException("Could not read BOM artifact " + bomArtifact.toString(), ex);
        }
    }

    protected void processProperties(Artifact bomArtifact) throws IOException, ArtifactResolutionException, ArtifactNotFoundException {
        getLog().debug("Processing BOM artifact " + bomArtifact);
        artifactResolver.resolve(bomArtifact, remoteRepos, localRepo);

        File bomFile = bomArtifact.getFile();
        MavenProject bomProject = readBareProject(bomFile);
        bomProject.getProperties().forEach(
                (Object key, Object value) -> project.getProperties().putIfAbsent(key, value)
        );

        if (recursive) {
            Artifact parentArtifact = bomProject.getParentArtifact();
            if (parentArtifact != null) {
                processProperties(parentArtifact);
            }
        }
    }

    // see http://stackoverflow.com/questions/951643/collapsing-parent-pom-into-the-child/1001575#1001575
    public static MavenProject readBareProject(final File file) throws IOException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(new FileReader(file));
        } catch (IOException | XmlPullParserException ex) {
            throw new IOException("Cannot parse BOM artifact", ex);
        }

        return new MavenProject(model);
    }
}
