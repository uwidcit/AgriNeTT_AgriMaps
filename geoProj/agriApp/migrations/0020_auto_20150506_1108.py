# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0019_auto_20150501_1031'),
    ]

    operations = [
        migrations.AddField(
            model_name='soilfeatures',
            name='SoilOrderName',
            field=models.CharField(max_length=80, null=True),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='soilfeatures',
            name='SoilSeriesName',
            field=models.CharField(max_length=80, null=True),
            preserve_default=True,
        ),
    ]
