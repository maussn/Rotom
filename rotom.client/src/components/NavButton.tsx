import { type To, useNavigate } from "react-router-dom";

type Props = {
    to: To;
    text: string;
    isActive: boolean;
};
export const NavButton = (props: Props) => {
    const { to, text } = props;
    const navigate = useNavigate();

    return (<button onClick={() => navigate(to)}>
        {text}
    </button>)
}
