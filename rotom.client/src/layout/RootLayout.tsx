import { Outlet, useLocation } from "react-router-dom";
import logo from "../assets/0479Rotom-Mow.png";
import { NavButton } from "../components/NavButton";
import { LoginMenu } from "../components/LoginMenu";

export const RootLayout = () => {
  const { pathname } = useLocation();

  return <div>
    <header id="logo-header">
      <img src={logo}/>
      <nav className="navbar">
        <NavButton to="/" text="catalogue" isActive={pathname === "/"} />
        <NavButton to="/about" text="about" isActive={pathname === "/"} />
      </nav>
      <LoginMenu/>
    </header>

    <main className="flex-1">
      <Outlet />
    </main>
  </div>
};
