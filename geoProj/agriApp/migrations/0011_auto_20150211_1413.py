# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0010_soilfeatures'),
    ]

    operations = [
        migrations.AlterField(
            model_name='soilfeatures',
            name='horizon_to_dept',
            field=models.FloatField(default=0.0),
            preserve_default=True,
        ),
    ]
