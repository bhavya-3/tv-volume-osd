# Threat model

| Asset | Threat | Mitigation | Residual risk |
|---|---|---|---|
| User input | Overlay captures or obscures interaction | Future overlay will be non-focusable and non-touchable, time-limited, and display fixed local data only | Android overlays are security-sensitive capabilities |
| App process | Malicious intent invocation | Components are non-exported except the required launcher activity; no IPC surface | Launcher activity can be started normally |
| Privacy | Data exfiltration | No `INTERNET` permission, telemetry, accounts, or identifiers | Android system metadata remains outside app control |
| System stability | Rapid audio changes cause work amplification | Fixed-rate bounded monitoring and timeout coalescing are planned | Vendor audio behavior can vary |
| Build integrity | Dependency/CI compromise | Minimal pinned dependencies and planned least-privilege CI | Upstream tooling remains a supply-chain dependency |
| Debug data | Logs leak sensitive state | Debug logging is limited to local volume diagnostics; release logging will be minimized | Users can share logs themselves |
