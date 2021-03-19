/*
 * (c) Copyright 2021 Felipe Orozco, Robert Kruszewski. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gradlets.baseline.typescript;

import com.palantir.logsafe.Preconditions;
import java.util.function.Consumer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugins.ide.idea.IdeaPlugin;

public class BaselineTypeScript implements Plugin<Project> {
    @Override
    public final void apply(Project project) {
        Project rootProject = project.getRootProject();
        Preconditions.checkState(
                project.equals(rootProject), "com.gradlets.baseline-typescript must be applied to the root project");

        applyToAllTypeScript(project, subProj -> {
            subProj.getPluginManager().apply(IdeaPlugin.class);
            subProj.getPluginManager().apply(BaselineTsc.class);
            subProj.getPluginManager().apply(BaselineLintPlugin.class);
            subProj.getPluginManager().apply(BaselineWebpack.class);
        });

        project.getPluginManager()
                .withPlugin(
                        "com.palantir.consistent-versions",
                        _plugin -> applyToAllTypeScript(
                                project, subproj -> subproj.getPluginManager().apply(BaselineVersions.class)));
    }

    private static void applyToAllTypeScript(Project project, Consumer<Project> projectAction) {
        project.allprojects(proj ->
                proj.getPluginManager().withPlugin("com.gradlets.typescript", _plugin -> projectAction.accept(proj)));
    }
}
