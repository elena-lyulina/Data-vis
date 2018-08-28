package org.intellij.datavis.data;

import com.intellij.openapi.project.Project;

public class DataProviderWrapper {
    public static DataProvider getProvider(Project project) {
        return DataProvider.getInstance(project);
    }
}
