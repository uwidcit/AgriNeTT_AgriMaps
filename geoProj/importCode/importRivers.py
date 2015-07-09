from django.contrib.gis.utils import LayerMapping
from models import Pipelines

riversJsonFile = '/Users/kiran/Downloads/rivers.json'
mapping = {
	'objectID':'RIVNM',
	'river25_Id':'RIVER25_ID',
	'river25':'RIVER25_',
	'rpoly':'RPOLY_',
	'lpoly':'LPOLY_',
	'tnode':'TNODE_',
	'fnode':'FNODE_',
	'length':'LENGTH',
	'geomData':'MULTILINESTRING'
}

lm = LayerMapping(Rivers,riversJsonFile,mapping)
lm.save(verbose=True)