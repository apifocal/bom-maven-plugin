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
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ImportBomMojoTest extends AbstractMojoTestCase {

//    @Override
//    protected void setUp() throws Exception {
//        // required
//        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        // required
//        super.tearDown();
//    }
//
    public void testTheMojo() throws Exception {
        File pom = getTestFile("src/test/resources/test-project.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        ImportBomMojo myMojo = (ImportBomMojo) lookupMojo("import-bom", pom);
        assertNotNull(myMojo);

        assertEquals("org.example", myMojo.bomArtifact.getGroupId());
        assertEquals("bom", myMojo.bomArtifact.getArtifactId());
        assertEquals("0.1.0-SNAPSHOT", myMojo.bomArtifact.getVersion());

        // apparently the project is not injected in unit tests, so this below is quite useless
//        myMojo.execute();
//
//        MavenProject prj = myMojo.project;
//        Properties props = prj.getProperties();
//        assertEquals("1.0.0-SNAPSHOT", props.getProperty("dummy-artifact.version")); // from bom-project
//        assertEquals("1.2.0-SNAPSHOT", props.getProperty("dummy-artifact-2.version")); // from test-project
//
//        List<Dependency> dependencies = prj.getDependencyManagement().getDependencies();
//        assertEquals(3, dependencies.size());
//        dependencies.forEach((Dependency dep) -> {
//            switch (dep.getArtifactId()) {
//                case "dummy-artifact":
//                    assertEquals("1.0.0-SNAPSHOT", dep.getVersion());
//                    break;
//                case "dummy-artifact-2":
//                    // gets the value from the bom, even if the importing project changes it
//                    assertEquals("2.0.0-SNAPSHOT", dep.getVersion());
//                    break;
//                case "dummy-artifact-3":
//                    // this one does not have a property
//                    assertEquals("1.1.0-SNAPSHOT", dep.getVersion());
//                    break;
//            }
//        }
//        );
    }
}
