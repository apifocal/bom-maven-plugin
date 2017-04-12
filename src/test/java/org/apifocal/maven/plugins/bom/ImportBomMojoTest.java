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
import org.apache.maven.model.Dependency;
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
    public void testImportPropertiesMojo() throws Exception {
        File pom = getTestFile("src/test/resources/test-project.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        ImportPropertiesMojo myMojo = (ImportPropertiesMojo) lookupMojo("import-properties", pom);
        assertNotNull(myMojo);

        assertEquals(1, myMojo.artifacts.size());

        Dependency art1 = myMojo.artifacts.get(0);
        assertEquals("org.example", art1.getGroupId());
        assertEquals("bom", art1.getArtifactId());
        assertEquals("0.1.0-SNAPSHOT", art1.getVersion());
    }
}
