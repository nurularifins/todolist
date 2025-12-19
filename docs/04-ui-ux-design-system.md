# UI/UX Design System & Theme Guide

This document outlines the **"HealDocs" / "Premium Blue"** design system implemented in the TodoList application. It defines the visual standards, component behaviors, and specific CSS techniques used to achieve the modern, premium aesthetic.

## 1. Core Philosophy
-   **Modern & Clean**: Minimalist layout with generous whitespace.
-   **Glassmorphism**: Use of semi-transparent backgrounds with backdrop blur to create depth and hierarchy.
-   **Premium Blue Theme**: A curated palette of primary blues (`blue-600`), soft backgrounds (`gray-50`), and vibrant accents.

## 2. Key Interface Components

### Sidebar Navigation
The sidebar is designed to be unobtrusive yet accessible, adapting to different screen sizes.
-   **Desktop**: Fixed to the left, full height.
-   **Mobile**: Collapsible off-canvas menu with smooth transitions.
-   **Visual Style**: **Glassmorphism**.
    -   It uses a white background with reduced opacity (`bg-white/80` or similar).
    -   Applied **Backdrop Blur** (`backdrop-blur-md`) to ensure legible text over any underlying content while maintaining a modern, airy feel.
    -   Active links are highlighted with a soft blue background (`bg-blue-50`) and bold blue text (`text-blue-600`).

### Sticky Header
The top navigation bar remains accessible as the user scrolls, ensuring critical actions (Search, Profile, Notifications) are always within reach.
-   **Behavior**: `sticky top-0 z-40` logic ensures it pins to the top of the viewport.
-   **Visual Style**:
    -   **Translucent Background**: White background with slight transparency (`bg-white/90`).
    -   **Backdrop Blur**: Strong blur (`backdrop-blur-md`) blurs the content scrolling underneath, preventing visual clutter while maintaining context.
    -   **Shadow**: A subtle bottom border or shadow separates it from the content area.

### Dashboard & Task Board
-   **Gradient Banners**: Use of linear gradients (e.g., `bg-gradient-to-r from-blue-600 to-yellow-200`) to create visually striking welcome areas.
-   **Task Cards**:
    -   Displayed in a responsive grid.
    -   **Color Cycling**: Task cards cycle through a predefined set of pastel border colors (Blue, Purple, Orange, Pink) to reduce visual monotony.
    -   **Hover Effects**: Subtle lift and shadow increase (`hover:-translate-y-1`, `hover:shadow-lg`) provide tactile feedback.

### Forms & Interactions
-   **Input Fields**: Standardized with `rounded-lg`, `border-gray-200`, and a `focus:ring-2 focus:ring-blue-500` outline.
-   **Datepicker**: Integrated **Flatpickr** with a "Material Blue" theme for a premium date-selection experience.
-   **Reveal Password**: Custom Alpine.js implementation allowing users to toggle password visibility in security forms.
-   **Reveal Password**: Custom Alpine.js implementation allowing users to toggle password visibility in security forms.
-   **Toast Notifications**:
    -   **Position**: Fixed top-right (`fixed top-4 right-4 z-50`).
    -   **Animation**: Smooth slide-in from top/right (`x-transition:enter`).
    -   **Behavior**: Auto-dismisses after 4 seconds using Alpine.js `x-init` logic.
    -   **Visuals**: Color-coded (Green for success, Red for error) with icons and shadow depth.
## 3. Technical Implementation Details
-   **Framework**: Tailwind CSS is the primary utility-first framework.
-   **Interactivity**: Alpine.js handles lightweight front-end logic (Dropdowns, Modals, Toast Notifications, Password Toggles).
-   **Icons**: Heroicons (via SVG) are used consistently for all iconography.
