import { createContext, useContext, useState } from "react";
import type { ReactNode } from "react";
import { sendLoginRequest } from "../services/api";

interface AuthContextType {
  isLoggedIn: boolean
  username: string | null
  id: string | null
  login: (username: string, password: string) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isLoggedIn, setLoggedIn] = useState(false)
  const [username, setUsername] = useState<string | null>(null)
  const [id, setId] = useState<string | null>(null)

  const login = async (name: string, password: string) => {
    const body = await sendLoginRequest(name, password)
    if (body.userId) {
      setLoggedIn(true)
      setUsername(name)
      setId(body.userId)
    } else {

    }
  }

  const logout = () => {
    setLoggedIn(false)
    setUsername(null)
    setId(null)
  }

  return (
    <AuthContext.Provider value={{ isLoggedIn, username, id, login, logout}}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};