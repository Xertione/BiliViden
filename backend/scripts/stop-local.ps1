$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$backendDir = Join-Path $repoRoot "backend"
$runtimeDir = Join-Path $backendDir "target\\local-run"
$pidFile = Join-Path $runtimeDir "powershell.pid"
$starterPidFile = Join-Path $runtimeDir "starter.pid"

if (-not (Test-Path $pidFile)) {
    Write-Output "No PID file found."
} else {
    $runnerPid = Get-Content $pidFile | Select-Object -First 1
    if (-not $runnerPid) {
        Remove-Item -LiteralPath $pidFile -Force -ErrorAction SilentlyContinue
        Write-Output "PID file was empty."
    } else {
        $process = Get-Process -Id $runnerPid -ErrorAction SilentlyContinue
        if ($process) {
            Stop-Process -Id $runnerPid -Force
            Write-Output "Stopped runner PID $runnerPid."
        } else {
            Write-Output "Runner PID $runnerPid was not active."
        }
    }
}

if (Test-Path $starterPidFile) {
    $starterPid = Get-Content $starterPidFile | Select-Object -First 1
    if ($starterPid) {
        $starter = Get-Process -Id $starterPid -ErrorAction SilentlyContinue
        if ($starter) {
            Stop-Process -Id $starterPid -Force -ErrorAction SilentlyContinue
            Write-Output "Stopped starter PID $starterPid."
        }
    }
}

Remove-Item -LiteralPath $pidFile -Force -ErrorAction SilentlyContinue
Remove-Item -LiteralPath $starterPidFile -Force -ErrorAction SilentlyContinue
