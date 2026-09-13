# User Service

Standalone multi-tenant Java microservice domain project.

## Quality & AI Review Pipeline Integration

This repository connects to the central **DevEx AI Code Review Pipeline** (`devex-ai-code-review`):
1. **Local Coverage Gate:** Every pull request runs `mvn clean verify` with JaCoCo. If line coverage is `< 80%`, the build fails immediately.
2. **Authenticated AI Review Dispatch:** When unit tests and coverage ($\ge 80\%$) pass, GitHub Actions dispatches an authenticated webhook with `X-Api-Key: ${{ secrets.DEVEX_PIPELINE_API_KEY }}` to the central DevEx review platform.
3. **Zero-Clone Review:** The central Step Functions pipeline analyzes PR diffs and walks AST call-graphs on demand via GitHub MCP without cloning this repository into AWS Lambda.
