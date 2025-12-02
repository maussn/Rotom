import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { postLoanRequest } from "../services/api";
import type { Item, LoanRequest } from "../types";
import ItemContainer from "./ItemContainer";

type Props = {
items: Item[];
};


export default function ItemScrollList({ items }: Props) {

  const {id, isLoggedIn} = useAuth()
  const navigate = useNavigate()

  function curriedHandleLoanRequest(item: Item): () => void {
    return function () {
      if (!isLoggedIn) {
        navigate("/login")
      } else {
        const dateStart = new Date()
        const dateEnd = new Date()
        dateEnd.setDate(dateStart.getDate() + 3)
        const req: LoanRequest = {
          item: item.id,
          borrower: id ?? "",
          dateStart: dateStart,
          dateEnd: dateEnd
        }
        postLoanRequest(req)
        navigate("/test")
      }
    }
  }

  return (
    <div className="items-list">
      {items.map((item) => (
        <ItemContainer key={item.id} item={item} handleLoanRequest={curriedHandleLoanRequest(item)} />
      ))}
    </div>
  );
}