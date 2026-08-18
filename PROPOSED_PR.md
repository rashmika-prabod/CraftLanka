## Description
This PR implements secure role-based authentication, integrates Cloudinary for media storage, and introduces the high-fidelity Seller Home Dashboard. It also includes project-wide refactoring to satisfy strict CI linting requirements and establishes repository code ownership.

## Changes Made
- **Authentication & Security**:
    - Enforced strict **Role-Based Access Control (RBAC)** in `BuyerLoginFragment` and `SellerLoginFragment` to prevent cross-portal access.
    - Integrated Firebase Authentication for secure email/password and Google Sign-In flows.
    - Fixed `BuildConfig` generation errors by sanitizing `local.properties` values in `build.gradle.kts`.
- **Media & Storage**:
    - Integrated **Cloudinary SDK** for secure, off-device storage of seller profile photos.
    - Implemented logic to dynamically display profile photos or name initials in the dashboard header.
    - Integrated **Glide** for optimized image loading and circular cropping.
- **Seller Home Dashboard UI**:
    - Implemented a complex 2x2 stats grid for business metrics (Products, Sales, Revenue, Low Stock).
    - Created a dynamic **Low Stock Alert** system with a reusable item layout (`item_low_stock.xml`).
    - Added a "Performance Snapshot" section and a "Subscription Plan" management card.
    - Built a custom **6-item Bottom Navigation Bar** with specialized active-state highlighting.
- **Code Quality & CI**:
    - Refactored all fragments to use the `bindingVar` / `binding` pattern to resolve `ktlint` backing-property violations.
    - Applied project-wide formatting via `spotlessApply` to fix trailing commas and spacing.
- **Collaboration**:
    - Updated `.github/CODEOWNERS` to grant `@seshan03` shared authority over buyer-side and data logic.

## Testing
- [x] Tested locally: Verified login/registration flows, role validation, and image uploads.
- [x] All tests pass: Verified via `:app:assembleDebug`.
- [x] Lint passes: Verified via `:app:spotlessCheck`.

## Checklist
- [x] My code follows the project style
- [ ] I have updated documentation if needed
- [x] No unnecessary files are included
- [x] CI checks pass

## Related Issue
Closes #
