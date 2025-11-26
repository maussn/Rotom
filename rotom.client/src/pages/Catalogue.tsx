import ItemScrollList from "../components/ItemScrollList"
import { getCatalogue } from "../services/api"
import { castToItems } from "../types"

const fetchedItems = await getCatalogue()
console.log(fetchedItems)

const items = castToItems(fetchedItems.items)

export const Catalogue = () => {
  return <div className="center-screen">
    <div className="box">
      <ItemScrollList items={items} />
    </div>
  </div>
}