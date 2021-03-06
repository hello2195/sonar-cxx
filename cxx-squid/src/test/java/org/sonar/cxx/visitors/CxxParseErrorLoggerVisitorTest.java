/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2017 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
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
package org.sonar.cxx.visitors;

import java.io.File;
import java.util.List;
import org.apache.commons.io.Charsets;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.utils.log.LogTester;
import org.sonar.cxx.CxxAstScanner;

public class CxxParseErrorLoggerVisitorTest {
  
  @org.junit.Rule
  public LogTester logTester = new LogTester();
  
  private SensorContextTester context;
  private File file;

  @Before
  @SuppressWarnings("unchecked")
  public void scanFile() {    
    String dir = "src/test/resources/visitors";

    file = new File(dir, "/syntaxerror.cc");
    DefaultInputFile inputFile = new DefaultInputFile("moduleKey", file.getName())
      .initMetadata(new FileMetadata().readMetadata(file, Charsets.UTF_8));

    context = SensorContextTester.create(new File(dir));
    context.fileSystem().add(inputFile);

    CxxAstScanner.scanSingleFile(inputFile, context);
  }

  @Test
  public void handleParseErrorTest() throws Exception {
    List<String> log = logTester.logs();
    assertThat(log.size()).isEqualTo(8);
    assertThat(log.get(3)).contains("skip declarartion: namespace X {");
    assertThat(log.get(4)).contains("skip declarartion: void test :: f1 ( ) {");
    assertThat(log.get(5)).contains("syntax error: i = unsigend int ( i + 1 )");
    assertThat(log.get(6)).contains("skip declarartion: void test :: f3 ( ) {");
    assertThat(log.get(7)).contains("syntax error: int i = 0 i ++");
  }
}
