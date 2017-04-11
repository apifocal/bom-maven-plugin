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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 */
public abstract class AbstractBomMojo extends AbstractMojo {

    protected static final String RESOLUTIONSCOPE_IMPORT = "import";

    //// standard maven properties
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    protected ArtifactRepository localRepo;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    protected List<ArtifactRepository> remoteRepos;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component
    private ProjectBuilder projectBuilder;

    @Component
    private RepositorySystem repoSystem;

    /**
     * Read a file as a {@link MavenProject}.
     *
     * @see
     * http://stackoverflow.com/questions/951643/collapsing-parent-pom-into-the-child/1001575#1001575
     */
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

    protected void processProperties(Artifact bomArtifact) throws IOException, ProjectBuildingException {
        getLog().debug("Processing BOM artifact " + bomArtifact);
        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(null);
        buildingRequest.setResolveDependencies(false);
        MavenProject bomProject = projectBuilder.build(bomArtifact, buildingRequest).getProject();
        bomProject.getProperties().forEach((Object key, Object value) -> project.getProperties().putIfAbsent(key, value));
    }

    protected void importProperties(Dependency artifactMetadata) throws MojoExecutionException {
        try {
            Artifact artifact = repoSystem.createDependencyArtifact(artifactMetadata);
            processProperties(artifact);
        } catch (IOException | ProjectBuildingException ex) {
            getLog().error("Failed to resolve artifact", ex);
            throw new MojoExecutionException("Could not read artifact " + artifactMetadata.toString(), ex);
        }
    }
}
