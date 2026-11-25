/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        forest: "#0b4b3e",
        pine: "#083c32",
        sage: "#e9f0e7",
        mint: "#cfe6d0",
        cream: "#fbfaf5",
        ink: "#17332b",
        coral: "#e89b75",
        lilac: "#d8d8ee",
      },
      fontFamily: {
        sans: ["Inter", "ui-sans-serif", "system-ui"],
        display: ["DM Sans", "Inter", "sans-serif"],
      },
      boxShadow: {
        soft: "0 20px 60px rgba(11,75,62,.09)",
        card: "0 8px 30px rgba(11,75,62,.06)",
      },
      borderRadius: { "4xl": "2rem" },
    },
  },
  plugins: [],
};
