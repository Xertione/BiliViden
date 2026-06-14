$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$backendDir = Join-Path $repoRoot "backend"
$runtimeDir = Join-Path $backendDir "target\\local-run"
$logFile = Join-Path $runtimeDir "spring-boot.log"
$pidFile = Join-Path $runtimeDir "powershell.pid"
$runner = Join-Path $backendDir "scripts\\run-local-server.ps1"
$starterPidFile = Join-Path $runtimeDir "starter.pid"
$stdoutFile = Join-Path $runtimeDir "starter.stdout.log"
$stderrFile = Join-Path $runtimeDir "starter.stderr.log"

New-Item -ItemType Directory -Force -Path $runtimeDir | Out-Null

if (Test-Path $pidFile) {
    $existingPid = Get-Content $pidFile | Select-Object -First 1
    if ($existingPid) {
        $existingProcess = Get-Process -Id $existingPid -ErrorAction SilentlyContinue
        if ($existingProcess) {
            throw "Backend runner is already active with PID $existingPid. Stop it first."
        }
    }
}

if (Test-Path $logFile) {
    Remove-Item -LiteralPath $logFile -Force
}

if (Test-Path $pidFile) {
    Remove-Item -LiteralPath $pidFile -Force
}

if (Test-Path $starterPidFile) {
    Remove-Item -LiteralPath $starterPidFile -Force
}

if (Test-Path $stdoutFile) {
    Remove-Item -LiteralPath $stdoutFile -Force
}

if (Test-Path $stderrFile) {
    Remove-Item -LiteralPath $stderrFile -Force
}

$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "powershell.exe"
$psi.Arguments = "-NoProfile -ExecutionPolicy Bypass -File `"$runner`""
$psi.WorkingDirectory = $repoRoot
$psi.UseShellExecute = $false
$psi.CreateNoWindow = $true

$process = [System.Diagnostics.Process]::Start($psi)
if (-not $process) {
    throw "Failed to start backend runner process."
}

Set-Content -Path $starterPidFile -Value $process.Id -Encoding ascii

$started = $false
for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 1
    if (Test-Path $pidFile) {
        $started = $true
        break
    }
    if ($process.HasExited) {
        break
    }
}

$diagnostic = @()
if (Test-Path $stdoutFile) {
    $diagnostic += "STDOUT:"
    $diagnostic += Get-Content $stdoutFile -Tail 20
}
if (Test-Path $stderrFile) {
    $diagnostic += "STDERR:"
    $diagnostic += Get-Content $stderrFile -Tail 20
}

if (-not $started) {
    $exitInfo = ""
    if ($process.HasExited) {
        $exitInfo = "Runner process exited early with code $($process.ExitCode)."
    }
    throw ("Backend runner did not create PID file in time.`n$exitInfo`n" + ($diagnostic -join "`n"))
}

$runnerPid = Get-Content $pidFile | Select-Object -First 1
Write-Output "STARTER_PID=$($process.Id)"
Write-Output "RUNNER_PID=$runnerPid"
Write-Output "LOG_FILE=$logFile"
Write-Output "STDOUT_FILE=$stdoutFile"
Write-Output "STDERR_FILE=$stderrFile"
