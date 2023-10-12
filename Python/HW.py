import os
import sys

import constants 

from langchain.document_loaders import PyPDFLoader

loader = PyPDFLoader("C:/Users/wgm33/OneDrive/Documents/GitHub/Default/Python/data/resume.pdf")
pages = loader.load_and_split()

os.environ["OPENAI_API_KEY"] = constants.APIKEY

from langchain.vectorstores import FAISS
from langchain.embeddings.openai import OpenAIEmbeddings

faiss_index = FAISS.from_documents(pages, OpenAIEmbeddings())
docs = faiss_index.similarity_search("What experience is documented here for which dates?", k=2)
for doc in docs:
    print(str(doc.metadata["page"]) + ":", doc.page_content[:300])
           