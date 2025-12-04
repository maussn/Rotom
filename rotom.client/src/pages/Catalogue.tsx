import { useEffect, useState } from "react"
import ItemScrollList from "../components/ItemScrollList"
import { useAuth } from "../contexts/AuthContext"
import { getCatalogue, getCatalogueLoggedIn } from "../services/api"
import type { Item } from "../types"



export const Catalogue = () => {
  const { isLoggedIn, id } = useAuth()

  const [items, setItems] = useState<Item[]>([])

  useEffect(() => {
    const fetchData = async () => {
      try {
        const fetchedItems = isLoggedIn
          ? await getCatalogueLoggedIn(id ?? "")
          : await getCatalogue()

        setItems(fetchedItems ?? [])
      } catch (error) {
        console.error("Error fetching catalogue:", error)
      }
    }

    fetchData()
  }, [isLoggedIn, id])

  return <div>
    <div className="center-screen">
      <ItemScrollList items={items} />
    </div>
  </div>
  

}