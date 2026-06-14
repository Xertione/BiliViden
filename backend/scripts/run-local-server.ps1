$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$backendDir = Join-Path $repoRoot "backend"
$envFile = Join-Path $backendDir ".env.local"
$runtimeDir = Join-Path $backendDir "target\\local-run"
$logFile = Join-Path $runtimeDir "spring-boot.log"
$pidFile = Join-Path $runtimeDir "powershell.pid"
$javaHome = "C:\\tmp\\oracle-jdk-21"
$javaExe = Join-Path $javaHome "bin\\java.exe"
$jarFile = Get-ChildItem -Path (Join-Path $backendDir "target") -Filter "*.jar" |
    Where-Object { $_.Name -notlike "*.original" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

New-Item -ItemType Directory -Force -Path $runtimeDir | Out-Null
Set-Content -Path $pidFile -Value $PID -Encoding ascii

if (-not (Test-Path $envFile)) {
    throw "Missing local env file: $envFile"
}

if (-not (Test-Path $javaExe)) {
    throw "Missing Java 21 runtime: $javaExe"
}

if (-not $jarFile) {
    throw "Missing packaged jar under $backendDir\\target. Run package first."
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if (-not $line -or $line.StartsWith("#")) {
        return
    }

    $parts = $line -split "=", 2
    if ($parts.Count -eq 2) {
        [Environment]::SetEnvironmentVariable($parts[0], $parts[1], "Process")
    }
}

Set-Location $repoRoot
[Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Process")
[Environment]::SetEnvironmentVariable("Path", "$javaHome\\bin;$env:Path", "Process")

"[$(Get-Date -Format s)] starting backend from $($jarFile.Name)" | Tee-Object -FilePath $logFile -Append | Out-Null
& $javaExe -jar $jarFile.FullName *>&1 | Tee-Object -FilePath $logFile -Append
