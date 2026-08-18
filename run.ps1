
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Get-Content "$PSScriptRoot\.env" | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        if ($value) { [System.Environment]::SetEnvironmentVariable($name, $value, 'Process') }
    }
}

if (-not $env:TOKEN) {
    Write-Host "TOKEN manquant dans .env" -ForegroundColor Red
    exit 1
}
if (-not $env:DB_CONN_STRING) {
    Write-Host "DB_CONN_STRING manquant dans .env - configure ta base MongoDB avant de lancer le bot." -ForegroundColor Red
    exit 1
}

& "$PSScriptRoot\gradlew.bat" shadowJar
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$jar = Get-ChildItem "$PSScriptRoot\build\libs\*.jar" | Select-Object -First 1
& java -XX:ErrorFile=logs/hs_err_pid%p.log -Xmx2G -Xms500M -jar $jar.FullName
