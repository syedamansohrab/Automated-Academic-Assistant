import streamlit as st
import requests

# Set the page style
st.set_page_config(page_title="SmartCS Academic Assistant", layout="centered")

st.title("📚 SmartCS Search Engine")
st.write("Search through your academic documents and lecture notes.")

# The Search Bar
query = st.text_input("Enter your search query:", placeholder="e.g., BM25, semantic, compiler...")

if st.button("Search"):
    if query:
        with st.spinner("Searching documents..."):
            try:
                # Call our Java Backend API!
                response = requests.get(f"http://localhost:8080/search?q={query}")
                
                if response.status_code == 200:
                    results = response.json()
                    
                    if len(results) > 0:
                        st.success(f"Found {len(results)} matching documents!")
                        # Display the results nicely
                        for i, res in enumerate(results):
                            st.markdown(f"**{i+1}. {res['filename']}**")
                            st.caption(f"Relevance Score: {res['score']:.4f}")
                            st.divider()
                    else:
                        st.warning("No documents found matching your query.")
                else:
                    st.error("Error communicating with the search engine.")
            except Exception as e:
                st.error(f"Could not connect to backend. Is the Java server running? Error: {e}")
    else:
        st.warning("Please enter a search term.")