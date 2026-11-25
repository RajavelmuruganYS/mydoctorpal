import js from "@eslint/js";
import react from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";

const browser = Object.fromEntries(
  ["window", "document", "localStorage", "setTimeout", "clearTimeout", "setInterval", "clearInterval", "console", "URLSearchParams", "Intl", "Promise", "Math", "Date", "Number", "String", "Object", "Array", "JSON", "Set", "Boolean", "parseInt", "parseFloat", "isNaN", "Error", "FormData", "Blob", "URL"].map((n) => [n, "readonly"]),
);

export default [
  { ignores: ["dist", "node_modules"] },
  js.configs.recommended,
  {
    files: ["src/**/*.{js,jsx}"],
    plugins: { react, "react-hooks": reactHooks },
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      parserOptions: { ecmaFeatures: { jsx: true } },
      globals: { ...browser, import: "readonly" },
    },
    settings: { react: { version: "detect" } },
    rules: {
      "react/jsx-uses-vars": "error",
      "react/jsx-uses-react": "error",
      "react/jsx-no-undef": "error",
      "react-hooks/rules-of-hooks": "error",
      "react-hooks/exhaustive-deps": "warn",
      "no-unused-vars": ["error", { argsIgnorePattern: "^_", varsIgnorePattern: "^_" }],
      "no-undef": "error",
    },
  },
];
