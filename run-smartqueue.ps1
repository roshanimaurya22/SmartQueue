$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

Write-Host "`n=== SmartQueue local launcher ===" -ForegroundColor Cyan

if (-not (Test-Path -LiteralPath (Join-Path $PSScriptRoot 'pom.xml'))) {
    throw "pom.xml was not found. Extract the ZIP and run this helper from the SmartQueue folder."
}

try {
    $javaVersion = & java -version 2>&1
    if ($LASTEXITCODE -ne 0) { throw 'Java command failed.' }
    Write-Host 'Java detected:' -ForegroundColor Green
    $javaVersion | Select-Object -First 3 | ForEach-Object { Write-Host $_ }
} catch {
    Write-Host 'Java JDK 17 or newer is required, but the java command was not found.' -ForegroundColor Red
    Write-Host 'Install a JDK from https://adoptium.net/temurin/releases/ (Windows x64, JDK 17 or newer), then reopen this launcher.'
    exit 1
}

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    $mavenCommand = $mvn.Source
} else {
    $version = '3.9.9'
    $toolsDir = Join-Path $env:LOCALAPPDATA 'SmartQueueTools'
    $mavenHome = Join-Path $toolsDir "apache-maven-$version"
    $mavenCommand = Join-Path $mavenHome 'bin\mvn.cmd'

    if (-not (Test-Path -LiteralPath $mavenCommand)) {
        New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
        $zipPath = Join-Path $toolsDir "apache-maven-$version-bin.zip"
        $downloadUrl = "https://archive.apache.org/dist/maven/maven-3/$version/binaries/apache-maven-$version-bin.zip"
        Write-Host "Maven was not found on PATH. Downloading Maven $version for this user only..." -ForegroundColor Yellow
        Write-Host 'An internet connection is required for the first run.'
        Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath
        Expand-Archive -LiteralPath $zipPath -DestinationPath $toolsDir -Force
        Remove-Item -LiteralPath $zipPath -Force -ErrorAction SilentlyContinue
    }

    if (-not (Test-Path -LiteralPath $mavenCommand)) {
        throw "Maven download/extraction did not create $mavenCommand. Check your internet connection and try again."
    }
}

Write-Host "`nStarting Spring Boot. The first run may take a few minutes while dependencies download." -ForegroundColor Green
Write-Host 'When the log says Started SmartQueueApplication, open http://localhost:8080'
Write-Host 'Keep this window open while using the app. Press Ctrl+C to stop it.'
& $mavenCommand spring-boot:run
if ($LASTEXITCODE -ne 0) {
    Write-Host "`nApplication exited with an error. Review the Maven/Spring Boot error above." -ForegroundColor Red
    exit $LASTEXITCODE
}
