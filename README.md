# TargetFrameworkMigrator
Simple tool to migrate Solution wide the target framework for Visual Studio C# Projects.

## Requirements
[Java 8 or higher](https://java.com/en/download/)

## Usage
[Download](https://github.com/Xpitfire/TargetFrameworkMigrator/releases) release java file.

Open command prompt or PowerShell and execute:
```
java -jar migrator.jar -path=<path-to-root-dir> -recursive=<true/false> -version=<version-string>
```

Parameter | Description
--- | ---
-path | root path of the solution
-version |  parameter for defining the destination path
-recursive | optional: by default recursive is set to false, to prevent unwanted recursive descent

## Example

```
java -jar migrator.jar -path="C:/Worspace/Demo" -version="v4.6.2" -recursive=true
```
