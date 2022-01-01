@echo off & setlocal enabledelayedexpansion

:: Load properties.
for /f "tokens=1,2 delims==" %%g in (setup.properties) do (set %%g=%%h)

:: Convert name to lower case for artifact ID.
for /f "delims=" %%i in ('powershell -command "\"%name%\".toLower()"') do (set artifactId=%%i)

echo name=%name%, artifactId=%artifactId%

:: Note: All the PowerShell commands are garbage run-on things because newlines aren't being properly escaped on my system.
:: I specifically did not use exclusively PowerShell because Windows disables execution of PS scripts by default.
:: The whole idea of this is that all you do is clone the repo and run the script, don't want to add weird steps.
:: Older versions of PS also don't support the technique used to effectively turn a batch script into a PS script.

:: Replace values in pom.
powershell -command "$path = Resolve-Path ..\\pom.xml; (Get-Content -path $path -raw) -replace 'genericartifact','%artifactId%' -replace 'GenericAddon','%name%' -replace 'GenericDescription','%description%' -replace 'GenericAuthor','%author%' | Set-Content $path"

:: Replace values in appveyor configuration.
powershell -command "$path = Resolve-Path ..\\appveyor.yml; (Get-Content -path $path -raw) -replace 'GenericAddon','%name%' | Set-Content $path"

:: Replace values in basic JavaPlugin, rewriting to new location.
:: Note: Uses File::WriteAllLines because encoding UTF8NoBOM is not available on earlier PS versions. BOM causes compiler to break.
powershell -command "$basedir = Resolve-Path ..\\src\\main\\java\\com\\github\\gpaddons; $oldFile = Join-Path -Path $basedir -ChildPath genericartifact\GenericAddon.java; $newFile = Join-Path -Path $basedir -ChildPath %artifactId%\%name%.java; $content = (Get-Content -path $oldFile -raw) -replace 'genericartifact','%artifactId%' -replace 'GenericAddon','%name%'; [IO.File]::WriteAllLines((New-Item -Path $newFile -Force), $content)"

:: Delete old package and setup stuff. If you made the new name GenericArtifact you're a big jerk.
RMDIR /S /Q ..\src\main\java\com\github\gpaddons\genericartifact

echo Setup complete! Delete scripts folder at your convenience.
