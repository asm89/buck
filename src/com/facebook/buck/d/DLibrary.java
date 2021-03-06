/*
 * Copyright 2015-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.d;

import com.facebook.buck.cxx.CxxDescriptionEnhancer;
import com.facebook.buck.cxx.CxxPlatform;
import com.facebook.buck.cxx.Linker;
import com.facebook.buck.cxx.NativeLinkable;
import com.facebook.buck.cxx.NativeLinkableInput;
import com.facebook.buck.model.BuildTargets;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.BuildTargetSourcePath;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.rules.TargetGraph;
import com.facebook.buck.rules.Tool;
import com.facebook.buck.rules.coercer.FrameworkPath;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

public class DLibrary extends DCompileBuildRule implements NativeLinkable {
  BuildRuleParams params;
  BuildRuleResolver buildRuleResolver;

  public DLibrary(
      BuildRuleParams params,
      SourcePathResolver sourcePathResolver,
      BuildRuleResolver buildRuleResolver,
      ImmutableSortedSet<SourcePath> srcs,
      Tool compiler) {
    super(
        params,
        sourcePathResolver,
        srcs,
        ImmutableList.of("-lib"),
        compiler,
        BuildTargets.getGenPath(
            params.getBuildTarget(), "%s/lib" + params.getBuildTarget().getShortName() + ".a"));

    this.params = params;
    this.buildRuleResolver = buildRuleResolver;
  }

  @Override
  public NativeLinkableInput getNativeLinkableInput(
      TargetGraph targetGraph,
      CxxPlatform cxxPlatform,
      Linker.LinkableDepType type) {

    // Ensure the description for this rule has been generated.
    BuildRule buildRule = CxxDescriptionEnhancer.requireBuildRule(
        targetGraph,
        params,
        buildRuleResolver,
        cxxPlatform.getFlavor(),
        CxxDescriptionEnhancer.STATIC_FLAVOR);

    // Generate the linker arguments required to link with this library.
    ImmutableList<String> linkerArgs = ImmutableList.of(getPathToOutput().toString());

    return NativeLinkableInput.of(
        ImmutableList.of(new BuildTargetSourcePath(buildRule.getBuildTarget())),
        linkerArgs,
        ImmutableSet.<FrameworkPath>of(),
        ImmutableSet.<FrameworkPath>of());
  }

  @Override
  public NativeLinkable.Linkage getPreferredLinkage(CxxPlatform cxxPlatform) {
    return Linkage.ANY;
  }

  @Override
  public ImmutableMap<String, SourcePath> getSharedLibraries(
      TargetGraph targetGraph,
      CxxPlatform cxxPlatform) {
    return ImmutableMap.of();
  }
}
