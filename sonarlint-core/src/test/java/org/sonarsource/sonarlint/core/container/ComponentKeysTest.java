/*
 * SonarLint Core - Implementation (trimmed)
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.sonarlint.core.container;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentKeysTest {

  ComponentKeys keys = new ComponentKeys();

  @Test
  public void generate_key_of_class() {
    assertThat(keys.of(FakeComponent.class)).isEqualTo(FakeComponent.class);
  }

  @Test
  public void generate_key_of_object() {
    assertThat(keys.of(new FakeComponent())).isEqualTo("org.sonarsource.sonarlint.core.container.ComponentKeysTest.FakeComponent-fake");
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_throw_if_toString_is_not_overridden() {
    keys.of(new Object());

    // only on non-first runs, to avoid false-positives on singletons
    keys.of(new Object());
  }

  static class FakeComponent {
    @Override
    public String toString() {
      return "fake";
    }
  }
}
