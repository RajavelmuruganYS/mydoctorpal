import { createContext, useContext } from "react";
const C = createContext({ theme: "light" });
export const ThemeProvider = ({ children }) => (
  <C.Provider value={{ theme: "light" }}>{children}</C.Provider>
);
export const useTheme = () => useContext(C);
