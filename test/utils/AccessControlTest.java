/**
 * Yobi, Project Hosting SW
 *
 * Copyright 2012 NAVER Corp.
 * http://yobi.io
 *
 * @Author Hwi Ahn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils;

import models.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import play.test.Helpers;

import static org.fest.assertions.Assertions.assertThat;

import models.enumeration.Operation;
import models.enumeration.State;

public class AccessControlTest extends ModelTest<Role>{
    @Test
    public void isAllowed_siteAdmin() {
        // Given
        User admin = User.findByLoginId("admin");
        Project projectYobi = Project.findByOwnerAndProjectName("yobi", "projectYobi");

        // When
        boolean canUpdate = AccessControl.isAllowed(admin, projectYobi.asResource(), Operation.UPDATE);
        boolean canRead = AccessControl.isAllowed(admin, projectYobi.asResource(), Operation.READ);
        boolean canDelete = AccessControl.isAllowed(admin, projectYobi.asResource(), Operation.DELETE);

        // Then
        assertThat(canRead).isEqualTo(true);
        assertThat(canUpdate).isEqualTo(true);
        assertThat(canDelete).isEqualTo(true);
    }

    @Test
    public void isAllowed_projectCreator() {
        // Given
        User yobi = User.findByLoginId("yobi");
        Project projectYobi = Project.findByOwnerAndProjectName("yobi", "projectYobi");

        // When
        boolean canUpdate = AccessControl.isAllowed(yobi, projectYobi.asResource(), Operation.UPDATE);
        boolean canRead = AccessControl.isAllowed(yobi, projectYobi.asResource(), Operation.READ);
        boolean canDelete = AccessControl.isAllowed(yobi, projectYobi.asResource(), Operation.DELETE);

        // Then
        assertThat(canRead).isEqualTo(true);
        assertThat(canUpdate).isEqualTo(true);
        assertThat(canDelete).isEqualTo(true);
    }

    @Test
    public void isAllowed_notAMember() {
        // Given
        User notMember = User.findByLoginId("nori");
        Project projectYobi = Project.findByOwnerAndProjectName("yobi", "projectYobi");

        // When
        boolean canUpdate = AccessControl.isAllowed(notMember, projectYobi.asResource(), Operation.UPDATE);
        boolean canRead = AccessControl.isAllowed(notMember, projectYobi.asResource(), Operation.READ);
        boolean canDelete = AccessControl.isAllowed(notMember, projectYobi.asResource(), Operation.DELETE);

        // Then
        assertThat(canRead).isEqualTo(true);
        assertThat(canUpdate).isEqualTo(false);
        assertThat(canDelete).isEqualTo(false);
    }

    // AccessControl.isAllowed throws IllegalStateException if the resource
    // belongs to a project but the project is missing.
    @Test
    public void isAllowed_lostProject() {
        // Given
        User author = User.findByLoginId("nori");
        Project projectYobi = Project.findByOwnerAndProjectName("yobi", "projectYobi");
        Issue issue = new Issue();
        issue.setProject(projectYobi);
        issue.setTitle("hello");
        issue.setBody("world");
        issue.setAuthor(author);
        issue.state = State.OPEN;
        issue.save();

        // When
        issue.project = null;

        // Then
        try {
            AccessControl.isAllowed(author, issue.asResource(), Operation.READ);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }
}
